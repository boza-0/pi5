// clients.controller.js
const pool = require('../config/db');
const { isNonEmptyString } = require('../middleware/validation');

// Basic validators (use your middleware if you add them there)
function isEmail(value) {
  if (typeof value !== 'string') return false;
  const v = value.trim();
  return v.length <= 150 && /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
}
function isPhone(value) {
  if (value === undefined || value === null) return true; // optional
  if (typeof value !== 'string') return false;
  const v = value.trim();
  return v.length <= 30;
}
function isAddress(value) {
  if (value === undefined || value === null) return true; // optional
  if (typeof value !== 'string') return false;
  const v = value.trim();
  return v.length <= 255;
}

async function listClients(req, res) {
  try {
    const [rows] = await pool.query('SELECT * FROM clients ORDER BY id DESC');
    res.json(rows);
  } catch (err) {
    console.error('GET /clients error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function getClient(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) return res.status(400).json({ error: 'Invalid id' });
  try {
    const [rows] = await pool.query('SELECT * FROM clients WHERE id = ?', [id]);
    if (rows.length === 0) return res.status(404).json({ error: 'Not found' });
    res.json(rows[0]);
  } catch (err) {
    console.error('GET /clients/:id error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function createClient(req, res) {
  const { name, email, phone, address } = req.body;

  if (!isNonEmptyString(name, 100)) {
    return res.status(400).json({ error: 'Invalid name' });
  }
  if (!isEmail(email)) {
    return res.status(400).json({ error: 'Invalid email' });
  }
  if (!isPhone(phone)) {
    return res.status(400).json({ error: 'Invalid phone' });
  }
  if (!isAddress(address)) {
    return res.status(400).json({ error: 'Invalid address' });
  }

  try {
    const [result] = await pool.query(
      'INSERT INTO clients (name, email, phone, address) VALUES (?, ?, ?, ?)',
      [name.trim(), email.trim(), phone ? phone.trim() : null, address ? address.trim() : null]
    );
    const [rows] = await pool.query('SELECT * FROM clients WHERE id = ?', [result.insertId]);
    res.status(201).json(rows[0]);
  } catch (err) {
    // Handle unique email conflicts cleanly
    if (err && err.code === 'ER_DUP_ENTRY') {
      return res.status(409).json({ error: 'Email already exists' });
    }
    console.error('POST /clients error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function updateClient(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) {
    return res.status(400).json({ error: 'Invalid id' });
  }

  const { name, email, phone, address } = req.body;

  // Validate only provided fields
  if (name !== undefined && !isNonEmptyString(name, 100)) {
    return res.status(400).json({ error: 'Invalid name' });
  }
  if (email !== undefined && !isEmail(email)) {
    return res.status(400).json({ error: 'Invalid email' });
  }
  if (phone !== undefined && !isPhone(phone)) {
    return res.status(400).json({ error: 'Invalid phone' });
  }
  if (address !== undefined && !isAddress(address)) {
    return res.status(400).json({ error: 'Invalid address' });
  }

  try {
    const [existing] = await pool.query('SELECT * FROM clients WHERE id = ?', [id]);
    if (existing.length === 0) {
      return res.status(404).json({ error: 'Not found' });
    }

    const updated = {
      name: name !== undefined ? name.trim() : existing[0].name,
      email: email !== undefined ? email.trim() : existing[0].email,
      phone: phone !== undefined ? (phone ? phone.trim() : null) : existing[0].phone,
      address: address !== undefined ? (address ? address.trim() : null) : existing[0].address
    };

    try {
      await pool.query(
        'UPDATE clients SET name = ?, email = ?, phone = ?, address = ? WHERE id = ?',
        [updated.name, updated.email, updated.phone, updated.address, id]
      );
    } catch (err) {
      if (err && err.code === 'ER_DUP_ENTRY') {
        return res.status(409).json({ error: 'Email already exists' });
      }
      throw err;
    }

    const [rows] = await pool.query('SELECT * FROM clients WHERE id = ?', [id]);
    res.json(rows[0]);
  } catch (err) {
    console.error('PUT /clients/:id error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

async function deleteClient(req, res) {
  const id = Number(req.params.id);
  if (!Number.isInteger(id)) return res.status(400).json({ error: 'Invalid id' });
  try {
    const [result] = await pool.query('DELETE FROM clients WHERE id = ?', [id]);
    if (result.affectedRows === 0) return res.status(404).json({ error: 'Not found' });
    res.status(204).send();
  } catch (err) {
    console.error('DELETE /clients/:id error:', err);
    res.status(500).json({ error: 'Internal server error' });
  }
}

module.exports = { listClients, getClient, createClient, updateClient, deleteClient };
