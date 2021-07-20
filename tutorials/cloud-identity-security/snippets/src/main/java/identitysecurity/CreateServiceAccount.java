package identitysecurity;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.IamScopes;
import com.google.api.services.iam.v1.model.CreateServiceAccountRequest;
import com.google.api.services.iam.v1.model.ServiceAccount;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class CreateServiceAccount {

  public static void main(String[] args) {
    String projectID = "your-project-id";
    String serviceAccountName = "service-account-name";

    createServiceAccount(projectID, serviceAccountName);
  }

  /** Creates a service account.
   *
   * @param projectId: GCloud Project ID for which the service account should be created.
   * @param serviceAccountName: The name of the service account to be created.
   */
  public static void createServiceAccount(String projectId, String serviceAccountName) {

    Iam service = null;
    try {
      service = initService();
    } catch (IOException | GeneralSecurityException e) {
      System.out.println("Unable to initialize service: \n" + e.toString());
      return;
    }

    try {
      ServiceAccount serviceAccount = new ServiceAccount();
      serviceAccount.setDisplayName("your-display-name");
      CreateServiceAccountRequest request = new CreateServiceAccountRequest();
      request.setAccountId(serviceAccountName);
      request.setServiceAccount(serviceAccount);

      serviceAccount =
          service.projects().serviceAccounts().create("projects/" + projectId, request).execute();

      System.out.println("Created service account: " + serviceAccount.getEmail());
    } catch (IOException e) {
      System.out.println("Unable to create service account: \n" + e.toString());
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
            .setApplicationName("service-accounts")
            .build();
    return service;
  }
}