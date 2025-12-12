// client.model.js
const db = require('../config/db');

const Client = {
  getAll: async () => {
    const [rows] = await db.query('SELECT * FROM clients');
    return rows;
  },

  getById: async (id) => {
    const [rows] = await db.query('SELECT * FROM clients WHERE id = ?', [id]);
    return rows[0];
  },

  create: async (client) => {
    const { name, email, phone, address } = client;
    const [result] = await db.query(
      'INSERT INTO clients (name, email, phone, address) VALUES (?, ?, ?, ?)',
      [name, email, phone, address]
    );
    return Client.getById(result.insertId);
  },

  update: async (id, client) => {
    const { name, email, phone, address } = client;
    await db.query(
      'UPDATE clients SET name=?, email=?, phone=?, address=? WHERE id=?',
      [name, email, phone, address, id]
    );
    return Client.getById(id);
  },

  delete: async (id) => {
    await db.query('DELETE FROM clients WHERE id=?', [id]);
  }
};

module.exports = Client;
