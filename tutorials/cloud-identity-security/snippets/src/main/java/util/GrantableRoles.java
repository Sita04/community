package util;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.IamScopes;
import com.google.api.services.iam.v1.model.QueryGrantableRolesRequest;
import com.google.api.services.iam.v1.model.QueryGrantableRolesResponse;
import com.google.api.services.iam.v1.model.Role;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GrantableRoles {

  public static void main(String[] args) throws Exception {

    String projectID = "project-id";
    String serviceAccount = "service-account@domain";

    viewGrantableRoles(projectID, serviceAccount);
  }


  public static void viewGrantableRoles(String projectID, String serviceAccount)
      throws IOException, GeneralSecurityException {

    String fullResourceName = String.format("//iam.googleapis.com/projects/%s/serviceAccounts/%s", projectID, serviceAccount);

    GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(IamScopes.CLOUD_PLATFORM));

    Iam service =
        new Iam.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credential))
            .setApplicationName("grantable-roles")
            .build();

    QueryGrantableRolesRequest request = new QueryGrantableRolesRequest();
    request.setFullResourceName(fullResourceName);

    QueryGrantableRolesResponse response = service.roles().queryGrantableRoles(request).execute();

    for (Role role : response.getRoles()) {
      System.out.println("Title: " + role.getTitle());
      System.out.println("Name: " + role.getName());
      System.out.println("Description: " + role.getDescription());
      System.out.println();
    }
  }
}