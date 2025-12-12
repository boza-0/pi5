// src/models/order.model.js
const db = require('../config/db');

function generateOrderNumber() {
  // Example: ORD-2025-12-07-000001 (date + ms suffix)
  const now = new Date();
  const yyyy = now.getFullYear();
  const mm = String(now.getMonth() + 1).padStart(2, '0');
  const dd = String(now.getDate()).padStart(2, '0');
  const ms = String(now.getTime()).slice(-6);
  return `ORD-${yyyy}-${mm}-${dd}-${ms}`;
}

const Order = {
  getAll: async () => {
    const [rows] = await db.query('SELECT * FROM orders ORDER BY id DESC');
    return rows;
  },

  getById: async (id) => {
    const [rows] = await db.query('SELECT * FROM orders WHERE id = ?', [id]);
    return rows[0];
  },

  create: async (order) => {
    // Minimal insert; DB defaults handle timestamps and totals.
    const {
      order_number,
      client_id,
      order_status = 'pending',
      payment_method = 'credit_card',
      currency_code = 'EUR',
      discount_amount = 0.0,
      shipping_address = null,
      billing_address = null,
      notes = null
    } = order;

    const ref = order_number && order_number.trim() ? order_number.trim() : generateOrderNumber();

    const [result] = await db.query(
      `INSERT INTO orders
       (order_number, client_id, order_status, payment_method, currency_code,
        discount_amount, shipping_address, billing_address, notes)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        ref,
        client_id,
        order_status,
        payment_method,
        currency_code,
        discount_amount,
        shipping_address,
        billing_address,
        notes
      ]
    );

    return Order.getById(result.insertId);
  },

  update: async (id, order) => {
    const {
      order_status,
      payment_method,
      currency_code,
      discount_amount,
      shipping_address,
      billing_address,
      notes
    } = order;

    await db.query(
      `UPDATE orders SET
         order_status = ?,
         payment_method = ?,
         currency_code = ?,
         discount_amount = ?,
         shipping_address = ?,
         billing_address = ?,
         notes = ?
       WHERE id = ?`,
      [
        order_status,
        payment_method,
        currency_code,
        discount_amount,
        shipping_address,
        billing_address,
        notes,
        id
      ]
    );

    return Order.getById(id);
  },

  delete: async (id) => {
    await db.query('DELETE FROM orders WHERE id = ?', [id]);
  }
};

module.exports = Order;
