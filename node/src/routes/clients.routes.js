// clients.routes.js
const express = require('express');
const router = express.Router();
const {
  listClients,
  getClient,
  createClient,
  updateClient,
  deleteClient
} = require('../controllers/clients.controller');

// GET all clients
router.get('/', listClients);

// GET one client by ID
router.get('/:id', getClient);

// CREATE new client
router.post('/', createClient);

// UPDATE client by ID
router.put('/:id', updateClient);

// DELETE client by ID
router.delete('/:id', deleteClient);

module.exports = router;
