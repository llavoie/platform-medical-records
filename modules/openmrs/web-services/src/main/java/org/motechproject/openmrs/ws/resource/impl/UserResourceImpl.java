package org.motechproject.openmrs.ws.resource.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.motechproject.openmrs.ws.HttpException;
import org.motechproject.openmrs.ws.OpenMrsInstance;
import org.motechproject.openmrs.ws.RestClient;
import org.motechproject.openmrs.ws.resource.UserResource;
import org.motechproject.openmrs.ws.resource.model.Person;
import org.motechproject.openmrs.ws.resource.model.Person.PersonSerializer;
import org.motechproject.openmrs.ws.resource.model.Role;
import org.motechproject.openmrs.ws.resource.model.Role.RoleSerializer;
import org.motechproject.openmrs.ws.resource.model.RoleListResult;
import org.motechproject.openmrs.ws.resource.model.User;
import org.motechproject.openmrs.ws.resource.model.UserListResult;
import org.motechproject.openmrs.ws.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserResourceImpl implements UserResource {

    private final RestClient restClient;
    private final OpenMrsInstance openmrsInstance;

    @Autowired
    public UserResourceImpl(RestClient restClient, OpenMrsInstance openmrsInstace) {
        this.restClient = restClient;
        this.openmrsInstance = openmrsInstace;
    }

    @Override
    public UserListResult getAllUsers() throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePath("/user?v=full"));
        return (UserListResult) JsonUtils.readJson(responseJson, UserListResult.class);
    }

    @Override
    public UserListResult queryForUsersByUsername(String username) throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePathWithParams("/user?q={username}&v=full",
                username));

        return (UserListResult) JsonUtils.readJson(responseJson, UserListResult.class);
    }

    @Override
    public User createUser(User user) throws HttpException {
        Gson gson = getGsonWithAdapters();
        String responseJson = restClient.postForJson(openmrsInstance.toInstancePath("/user"), gson.toJson(user));

        return (User) JsonUtils.readJson(responseJson, User.class);
    }

    private Gson getGsonWithAdapters() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Person.class, new PersonSerializer())
                .registerTypeAdapter(Role.class, new RoleSerializer()).create();
        return gson;
    }

    @Override
    public RoleListResult getAllRoles() throws HttpException {
        String responseJson = restClient.getJson(openmrsInstance.toInstancePath("/role?v=full"));
        return (RoleListResult) JsonUtils.readJson(responseJson, RoleListResult.class);
    }

    @Override
    public void updateUser(User user) throws HttpException {
        Gson gson = getGsonWithAdapters();
        String uuid = user.getUuid();
        user.setUuid(null);
        String requestJson = gson.toJson(user);

        restClient.postWithEmptyResponseBody(openmrsInstance.toInstancePathWithParams("/user/{uuid}", uuid),
                requestJson);
    }

}
