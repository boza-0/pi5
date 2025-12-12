const pool = require('../config/db');
const { isNonEmptyString, isPrice, isStock, isProviderId } = require('../middleware/validation');

async function listProducts(req, res) {
  try {
    const [rows] = await pool.query('SELECT * FROM products ORDER BY id DESC');
    res.json(rows);
  } catch (err) {
    console.error('GET /products error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function getProduct(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) return res.status(400).json({ error: 'Invalid id' });
  try {
    const [rows] = await pool.query('SELECT * FROM products WHERE id = ?', [id]);
    if (rows.length === 0) return res.status(404).json({ error: 'Not found' });
    res.json(rows[0]);
  } catch (err) {
    console.error('GET /products/:id error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function createProduct(req, res) {
  const { name, description, price, stock, provider_id } = req.body;

  if (!isNonEmptyString(name, 100)) {
    return res.status(400).json({ error: 'Invalid name' });
  }
  if (description && typeof description !== 'string') {
    return res.status(400).json({ error: 'Invalid description' });
  }

  const numPrice = Number(price);
  if (!isPrice(numPrice)) {
    return res.status(400).json({ error: 'Invalid price' });
  }

  const numStock = Number(stock);
  if (!isStock(numStock)) {
    return res.status(400).json({ error: 'Invalid stock' });
  }

  const numProvider = provider_id !== undefined ? Number(provider_id) : null;
  if (!isProviderId(numProvider)) {
    return res.status(400).json({ error: 'Invalid provider_id' });
  }

  try {
    const [result] = await pool.query(
      'INSERT INTO products (name, description, price, stock, provider_id) VALUES (?, ?, ?, ?, ?)',
      [name.trim(), description || null, numPrice, numStock, numProvider]
    );

    const [rows] = await pool.query('SELECT * FROM products WHERE id = ?', [result.insertId]);
    res.status(201).json(rows[0]);
  } catch (err) {
    console.error('POST /products error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function updateProduct(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) {
    return res.status(400).json({ error: 'Invalid id' });
  }

  const { name, description, price, stock, provider_id } = req.body;

  const numPrice = price !== undefined ? Number(price) : undefined;
  const numStock = stock !== undefined ? Number(stock) : undefined;
  const numProvider = provider_id !== undefined ? Number(provider_id) : undefined;

  if (name && !isNonEmptyString(name, 100)) {
    return res.status(400).json({ error: 'Invalid name' });
  }
  if (description && typeof description !== 'string') {
    return res.status(400).json({ error: 'Invalid description' });
  }
  if (numPrice !== undefined && !isPrice(numPrice)) {
    return res.status(400).json({ error: 'Invalid price' });
  }
  if (numStock !== undefined && !isStock(numStock)) {
    return res.status(400).json({ error: 'Invalid stock' });
  }
  if (numProvider !== undefined && !isProviderId(numProvider)) {
    return res.status(400).json({ error: 'Invalid provider_id' });
  }

  try {
    const [existing] = await pool.query('SELECT * FROM products WHERE id = ?', [id]);
    if (existing.length === 0) {
      return res.status(404).json({ error: 'Not found' });
    }

    const updated = {
      name: name !== undefined ? name.trim() : existing[0].name,
      description: description !== undefined ? description : existing[0].description,
      price: numPrice !== undefined ? numPrice : existing[0].price,
      stock: numStock !== undefined ? numStock : existing[0].stock,
      provider_id: numProvider !== undefined ? numProvider : existing[0].provider_id
    };

    await pool.query(
      'UPDATE products SET name = ?, description = ?, price = ?, stock = ?, provider_id = ? WHERE id = ?',
      [updated.name, updated.description, updated.price, updated.stock, updated.provider_id, id]
    );

    const [rows] = await pool.query('SELECT * FROM products WHERE id = ?', [id]);
    res.json(rows[0]);
  } catch (err) {
    console.error('PUT /products/:id error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function deleteProduct(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) return res.status(400).json({ error: 'Invalid id' });
  try {
    const [result] = await pool.query('DELETE FROM products WHERE id = ?', [id]);
    if (result.affectedRows === 0) return res.status(404).json({ error: 'Not found' });
    res.status(204).send();
  } catch (err) {
    console.error('DELETE /products/:id error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

module.exports = { listProducts, getProduct, createProduct, updateProduct, deleteProduct };
