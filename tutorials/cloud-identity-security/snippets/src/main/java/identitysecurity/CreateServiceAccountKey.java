package identitysecurity;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.IamScopes;
import com.google.api.services.iam.v1.model.CreateServiceAccountKeyRequest;
import com.google.api.services.iam.v1.model.ServiceAccountKey;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class CreateServiceAccountKey {

  public static void main(String[] args) {
    String projectID = "your-project-id";
    String serviceAccountName = "service-account-name";

    createKey(projectID, serviceAccountName);
  }

  /** Creates a key for a service account.
   *
   * @param projectId: GCloud Project ID.
   * @param serviceAccountName: Name of the service account under which the key should be created.
   */
  public static void createKey(String projectId, String serviceAccountName) {

    Iam service = null;
    try {
      service = initService();
    } catch (IOException | GeneralSecurityException e) {
      System.out.println("Unable to initialize service: \n" + e.toString());
      return;
    }

    String serviceAccountEmail = serviceAccountName + "@" + projectId + ".iam.gserviceaccount.com";

    try {
      ServiceAccountKey key =
          service
              .projects()
              .serviceAccounts()
              .keys()
              .create(
                  "projects/-/serviceAccounts/" + serviceAccountEmail,
                  new CreateServiceAccountKeyRequest())
              .execute();

      System.out.println("Created key: " + key.getName());
    } catch (IOException e) {
      System.out.println("Unable to create service account key: \n" + e.toString());
    }
  }

  private static Iam initService() throws GeneralSecurityException, IOException {
    // Use the Application Default Credentials strategy for authentication. For more info, see:
    // https://cloud.google.com/docs/authentication/production#finding_credentials_automatically
    GoogleCredentials credential =
        GoogleCredentials.getApplicationDefault()
            .createScoped(Collections.singleton(IamScopes.CLOUD_PLATFORM));
    // Initialize the IAM service, which can be used to send requests to the IAM API.
    Iam service =
        new Iam.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credential))
            .setApplicationName("service-account-keys")
            .build();
    return service;
  }
}