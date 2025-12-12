package com.demo.ui.services;

import com.demo.ui.models.Client;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

public class ClientService {
    private final ApiClient api = new ApiClient("http://localhost:3000");
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Client> getAllClients() throws Exception {
        String body = api.get("/clients");
        return Arrays.asList(mapper.readValue(body, Client[].class));
    }

    public Client getClient(int id) throws Exception {
        String body = api.get("/clients/" + id);
        return mapper.readValue(body, Client.class);
    }

    public Client createClient(String name, String email, String phone, String address) throws Exception {
        // id=0, createdAt/updatedAt null → backend will fill them
        String json = mapper.writeValueAsString(
            new Client(0, name, email, phone, address, null, null)
        );
        String body = api.post("/clients", json);
        return mapper.readValue(body, Client.class);
    }

    public Client updateClient(int id, String name, String email, String phone, String address) throws Exception {
        String json = mapper.writeValueAsString(
            new Client(id, name, email, phone, address, null, null)
        );
        String body = api.put("/clients/" + id, json);
        return mapper.readValue(body, Client.class);
    }

    public void deleteClient(int id) throws Exception {
        api.delete("/clients/" + id);
    }
}
