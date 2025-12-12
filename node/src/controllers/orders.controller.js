// src/controllers/orders.controller.js
const pool = require('../config/db');
const { isNonEmptyString } = require('../middleware/validation');

// --- Basic validators ---
function isCurrency(value) {
  if (typeof value !== 'string') return false;
  const v = value.trim().toUpperCase();
  return /^[A-Z]{3}$/.test(v); // ISO 4217 code
}
function isStatus(value) {
  const allowed = ['pending','paid','shipped','completed','cancelled'];
  return typeof value === 'string' && allowed.includes(value);
}
function isPaymentMethod(value) {
  const allowed = ['credit_card','paypal','bank_transfer','cash'];
  return typeof value === 'string' && allowed.includes(value);
}
function isAddress(value) {
  if (value === undefined || value === null) return true;
  if (typeof value !== 'string') return false;
  return value.trim().length <= 255;
}

// --- Orders CRUD ---
async function listOrders(req, res) {
  try {
    const [rows] = await pool.query('SELECT * FROM orders ORDER BY id DESC');
    res.json(rows);
  } catch (err) {
    console.error('GET /orders error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function getOrder(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) return res.status(400).json({ error: 'Invalid id' });
  try {
    const [rows] = await pool.query('SELECT * FROM orders WHERE id = ?', [id]);
    if (rows.length === 0) return res.status(404).json({ error: 'Not found' });
    res.json(rows[0]);
  } catch (err) {
    console.error('GET /orders/:id error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function createOrder(req, res) {
  const {
    client_id,
    order_status = 'pending',
    payment_method = 'credit_card',
    currency_code = 'EUR',
    discount_amount = 0,
    shipping_address,
    billing_address,
    notes
  } = req.body;

  if (!Number.isInteger(client_id)) {
    return res.status(400).json({ error: 'Invalid client_id' });
  }
  if (!isStatus(order_status)) {
    return res.status(400).json({ error: 'Invalid order_status' });
  }
  if (!isPaymentMethod(payment_method)) {
    return res.status(400).json({ error: 'Invalid payment_method' });
  }
  if (!isCurrency(currency_code)) {
    return res.status(400).json({ error: 'Invalid currency_code' });
  }
  if (!isAddress(shipping_address) || !isAddress(billing_address)) {
    return res.status(400).json({ error: 'Invalid address' });
  }

  try {
    const [result] = await pool.query(
      `INSERT INTO orders 
       (order_number, client_id, order_status, payment_method, currency_code,
        discount_amount, shipping_address, billing_address, notes)
       VALUES (UUID(), ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        client_id,
        order_status,
        payment_method,
        currency_code,
        discount_amount,
        shipping_address || null,
        billing_address || null,
        notes || null
      ]
    );
    const [rows] = await pool.query('SELECT * FROM orders WHERE id = ?', [result.insertId]);
    res.status(201).json(rows[0]);
  } catch (err) {
    console.error('POST /orders error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function updateOrder(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) return res.status(400).json({ error: 'Invalid id' });

  const {
    order_status,
    payment_method,
    currency_code,
    discount_amount,
    shipping_address,
    billing_address,
    notes
  } = req.body;

  if (order_status !== undefined && !isStatus(order_status)) {
    return res.status(400).json({ error: 'Invalid order_status' });
  }
  if (payment_method !== undefined && !isPaymentMethod(payment_method)) {
    return res.status(400).json({ error: 'Invalid payment_method' });
  }
  if (currency_code !== undefined && !isCurrency(currency_code)) {
    return res.status(400).json({ error: 'Invalid currency_code' });
  }
  if (shipping_address !== undefined && !isAddress(shipping_address)) {
    return res.status(400).json({ error: 'Invalid shipping_address' });
  }
  if (billing_address !== undefined && !isAddress(billing_address)) {
    return res.status(400).json({ error: 'Invalid billing_address' });
  }

  try {
    const [existing] = await pool.query('SELECT * FROM orders WHERE id = ?', [id]);
    if (existing.length === 0) return res.status(404).json({ error: 'Not found' });

    const updated = {
      order_status: order_status !== undefined ? order_status : existing[0].order_status,
      payment_method: payment_method !== undefined ? payment_method : existing[0].payment_method,
      currency_code: currency_code !== undefined ? currency_code : existing[0].currency_code,
      discount_amount: discount_amount !== undefined ? discount_amount : existing[0].discount_amount,
      shipping_address: shipping_address !== undefined ? shipping_address : existing[0].shipping_address,
      billing_address: billing_address !== undefined ? billing_address : existing[0].billing_address,
      notes: notes !== undefined ? notes : existing[0].notes
    };

    await pool.query(
      `UPDATE orders SET order_status=?, payment_method=?, currency_code=?, discount_amount=?, 
       shipping_address=?, billing_address=?, notes=? WHERE id=?`,
      [
        updated.order_status,
        updated.payment_method,
        updated.currency_code,
        updated.discount_amount,
        updated.shipping_address,
        updated.billing_address,
        updated.notes,
        id
      ]
    );

    const [rows] = await pool.query('SELECT * FROM orders WHERE id = ?', [id]);
    res.json(rows[0]);
  } catch (err) {
    console.error('PUT /orders/:id error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function deleteOrder(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) return res.status(400).json({ error: 'Invalid id' });
  try {
    const [result] = await pool.query('DELETE FROM orders WHERE id = ?', [id]);
    if (result.affectedRows === 0) return res.status(404).json({ error: 'Not found' });
    res.status(204).send();
  } catch (err) {
    console.error('DELETE /orders/:id error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

// --- Order products ---
async function listOrderProducts(req, res) {
  const orderId = Number(req.params.id);
  if (!Number.isInteger(orderId)) return res.status(400).json({ error: 'Invalid order id' });
  try {
    const [rows] = await pool.query('SELECT * FROM order_products WHERE order_id = ?', [orderId]);
    res.json(rows);
  } catch (err) {
    console.error('GET /orders/:id/products error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function addOrderProduct(req, res) {
  const orderId = Number(req.params.id);
  if (!Number.isInteger(orderId)) return res.status(400).json({ error: 'Invalid order id' });

  const { product_id, quantity, unit_price } = req.body;
  if (!Number.isInteger(product_id)) return res.status(400).json({ error: 'Invalid product_id' });
  if (!Number.isInteger(quantity) || quantity <= 0) return res.status(400).json({ error: 'Invalid quantity' });
  if (typeof unit_price !== 'number' || unit_price < 0) return res.status(400).json({ error: 'Invalid unit_price' });

  try {
    const [result] = await pool.query(
      'INSERT INTO order_products (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)',
      [orderId, product_id, quantity, unit_price]
    );
    const [rows] = await pool.query('SELECT * FROM order_products WHERE id = ?', [result.insertId]);
    res.status(201).json(rows[0]);
  } catch (err) {
    console.error('POST /orders/:id/products error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function deleteOrderProduct(req, res) {
  const orderId = Number(req.params.id);
  const productId = Number(req.params.itemId);
  if (!Number.isInteger(orderId) || !Number.isInteger(productId)) {
    return res.status(400).json({ error: 'Invalid id' });
  }
  try {
    const [result] = await pool.query(
      'DELETE FROM order_products WHERE order_id = ? AND id = ?',
      [orderId, productId]
    );
    if (result.affectedRows === 0) {
      return res.status(404).json({ error: 'Not found' });
    }
    res.status(204).send();
  } catch (err) {
    console.error('DELETE /orders/:id/products/:itemId error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

module.exports = {
  listOrders,
  getOrder,
  createOrder,
  updateOrder,
  deleteOrder,
  listOrderProducts,
  addOrderProduct,
  deleteOrderProduct
};
