package identitysecurity;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.iam.v1.Iam;
import com.google.api.services.iam.v1.Iam.Projects.ServiceAccounts.GetIamPolicy;
import com.google.api.services.iam.v1.Iam.Projects.ServiceAccounts.TestIamPermissions;
import com.google.api.services.iam.v1.IamScopes;
import com.google.api.services.iam.v1.model.Binding;
import com.google.api.services.iam.v1.model.CreateServiceAccountRequest;
import com.google.api.services.iam.v1.model.Policy;
import com.google.api.services.iam.v1.model.ServiceAccount;
import com.google.api.services.iam.v1.model.SetIamPolicyRequest;
import com.google.api.services.iam.v1.model.TestIamPermissionsRequest;
import com.google.api.services.iam.v1.model.TestIamPermissionsResponse;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class ServiceAccountPolicy {

  public static void main(String[] args) {
    String projectID = "your-project-id";
    String serviceAccountName = "service-account-name";

    setTestServiceAccountPermission(projectID, serviceAccountName);
    setServiceAccountPermissions(projectID, serviceAccountName);
  }

  /** Simulates the permissions set for the service account.
   *
   * @param projectId: GCloud Project ID.
   * @param serviceAccountName: Name of the service account.
   */
  public static void setTestServiceAccountPermission(String projectId, String serviceAccountName) {

    Iam service = null;
    try {
      service = initService();
    } catch (IOException | GeneralSecurityException e) {
      System.out.println("Unable to initialize service: \n" + e.toString());
      return;
    }

    try {
      String serviceAccountEmail = serviceAccountName + "@" + projectId + ".iam.gserviceaccount.com";

      // For a list of permissions, please refer: https://cloud.google.com/iam/docs/overview#permissions
      TestIamPermissionsRequest iamPermissionsRequest = new TestIamPermissionsRequest()
          .setPermissions(Collections.singletonList("accessapproval.requests.approve"));

      TestIamPermissionsResponse response = service
          .projects()
          .serviceAccounts()
          .testIamPermissions(String.format("projects/%s/serviceAccounts/%s", projectId, serviceAccountEmail), iamPermissionsRequest)
          .execute();

      System.out.println("The following permissions have been set up for testing: " + response.getPermissions());

    } catch (IOException e) {
      System.out.println("Unable to test permissions: \n" + e.toString());
    }
  }

  /** Set the specified bindings to the service account.
   *
   * @param projectId: GCloud Project ID.
   * @param serviceAccountName: Name of the service account.
   */
  public static void setServiceAccountPermissions(String projectId, String serviceAccountName) {

    Iam service = null;
    try {
      service = initService();
    } catch (IOException | GeneralSecurityException e) {
      System.out.println("Unable to initialize service: \n" + e.toString());
      return;
    }

    try {
      String serviceAccountEmail = serviceAccountName + "@" + projectId + ".iam.gserviceaccount.com";

      Binding binding = new Binding().setRole("role-name");
      Policy policy = new Policy().setBindings(Collections.singletonList(binding));
      SetIamPolicyRequest setIamPolicyRequest = new SetIamPolicyRequest().setPolicy(policy);

      Policy response = service
          .projects()
          .serviceAccounts()
          .setIamPolicy(String.format("projects/%s/serviceAccounts/%s", projectId, serviceAccountEmail), setIamPolicyRequest)
          .execute();

      System.out.println(response.getBindings());

    } catch (IOException e) {
      System.out.println("Unable to set permissions: \n" + e.toString());
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
