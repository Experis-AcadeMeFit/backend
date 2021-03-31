package me.fit.mefit.keysecurity.services;

import me.fit.mefit.models.RoleEnum;
import me.fit.mefit.models.User;
import me.fit.mefit.payload.request.SignupRequest;
import me.fit.mefit.payload.request.UserPatchRequest;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakUserService {

    private Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Value("${mefit.app.adminClientId}") String clientId;
    @Value("${mefit.app.adminClientSecret}") String secret;
    @Value("${mefit.app.keycloakAddress}") String address;
    @Value("${mefit.app.keycloakRealm}") String realm;
    @Value("${mefit.app.usingKeycloak}") boolean usingKeycloak;
    private Keycloak keycloak;

    private void createClient() {
        keycloak = KeycloakBuilder.builder()
                .serverUrl(address + "/auth")
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(secret)
                .resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
                .build();
    }

    public void createUser(SignupRequest user) {
        if (usingKeycloak) {
            if (keycloak == null) {
                createClient();
            }

            UserRepresentation keyUser = new UserRepresentation();
            keyUser.setUsername(user.getEmail());
            keyUser.setFirstName(user.getFirstname());
            keyUser.setLastName(user.getLastname());
            keyUser.setEmail(user.getEmail());
            keyUser.setEmailVerified(true);
            keyUser.setEnabled(true);

            Response response = keycloak.realm(realm).users().create(keyUser);
            String userId = CreatedResponseUtil.getCreatedId(response);
            updatePassword(userId, user.getPassword());

            RoleRepresentation userRole = keycloak.realm(realm).roles().get("USER").toRepresentation();
            keycloak.realm(realm).users().get(userId).roles().realmLevel().add(Collections.singletonList(userRole));
        }
    }

    public void updatePassword(String keycloakId, String password) {
        if (usingKeycloak) {
            if (keycloak == null) {
                createClient();
            }

            CredentialRepresentation credentials = new CredentialRepresentation();
            credentials.setTemporary(false);
            credentials.setType(CredentialRepresentation.PASSWORD);
            credentials.setValue(password);
            keycloak.realm(realm).users().get(keycloakId).resetPassword(credentials);
        }
    }

    public void updateUser(String keycloakId, UserPatchRequest userPatchRequest) {
        if (usingKeycloak) {
            if (keycloak == null) {
                createClient();
            }

            UserRepresentation keyUser = keycloak.realm(realm).users().get(keycloakId).toRepresentation();

            if ( userPatchRequest.getEmail() != null ) {
                keyUser.setEmail(userPatchRequest.getEmail());
                keyUser.setEmailVerified(true);
            }

            if (userPatchRequest.getFirstName() != null) {
                keyUser.setFirstName(userPatchRequest.getFirstName());
            }

            if (userPatchRequest.getLastName() != null) {
                keyUser.setLastName(userPatchRequest.getLastName());
            }

            keycloak.realm(realm).users().get(keycloakId).update(keyUser);
        }
    }

    public void updateRoles(String keycloakId, List<RoleEnum> roles) {
        if (usingKeycloak) {
            if (keycloak == null) {
                createClient();
            }

            // Convert our local enums to keycloak's RoleRepresentation
            List<RoleRepresentation> updateRoles = new ArrayList<>();
            for (RoleEnum role : roles) {
                updateRoles.add(keycloak.realm(realm).roles().get(role.name().substring(5)).toRepresentation());
            }

            List<RoleRepresentation> currentRoles = keycloak.realm(realm)
                    .users()
                    .get(keycloakId)
                    .roles()
                    .realmLevel()
                    .listAll();

            List<RoleRepresentation> removeRoles = compareLists(currentRoles, updateRoles);
            List<RoleRepresentation> addRoles = compareLists(updateRoles, currentRoles);

            keycloak.realm(realm).users().get(keycloakId).roles().realmLevel().remove(removeRoles);
            keycloak.realm(realm).users().get(keycloakId).roles().realmLevel().add(addRoles);
        }
    }

    public void deleteUser(User user) {
        if (usingKeycloak) {
            if (keycloak == null) {
                createClient();
            }

            keycloak.realm(realm).users().delete(user.getKeycloakId());
        }
    }

    // Return a list of roles that are in listA but not listB
    // Without the use of streams or anything fancy like that
    private List<RoleRepresentation> compareLists(List<RoleRepresentation> listA, List<RoleRepresentation> listB) {
        List<RoleRepresentation> returnList = new ArrayList<>();

        for (RoleRepresentation currentRole : listA) {
            RoleRepresentation foundRole = null;

            // Search through listB for this role
            for (RoleRepresentation updateRole: listB) {
                if (updateRole.getId().equals(currentRole.getId())) {
                    foundRole = updateRole;
                }
            }

            // The role wasn't in listB
            if (foundRole == null) {
                returnList.add(currentRole);
            }
        }
        return returnList;
    }
}
