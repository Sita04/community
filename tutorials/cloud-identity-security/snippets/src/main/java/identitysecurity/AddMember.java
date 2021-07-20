package identitysecurity;

import com.google.api.services.cloudresourcemanager.model.Binding;
import com.google.api.services.cloudresourcemanager.model.Expr;
import com.google.api.services.cloudresourcemanager.model.Policy;
import java.util.List;
import util.GetPolicy;

public class AddMember {

  public static void main(String[] args) {
    String projectID = "your-project-id";
    String role = "roles/role-name";
    String member = "user:name@domain";

    addMember(projectID, role, member);
  }

  /**
   * Adds a member to a pre-defined IAM role.
   *
   * @param projectID: GCloud Project ID
   * @param role: The pre-defined IAM role to add the member eg., roles/iam.securityAdmin
   * @param member: The email id of the user to add
   */
  public static void addMember(String projectID, String role, String member) {
    // Get the IAM policy for the current project ID.
    Policy policy = GetPolicy.getPolicy(projectID);
    List<Binding> bindings = policy.getBindings();

    // Iterate through the IAM bindings
    for (Binding binding : bindings) {
      // If the role matches and the binding doesn't contain the member, then add.
      if (binding.getRole().equals(role)) {
        if (!binding.getMembers().contains(member)) {
          // Add the member
          binding.getMembers().add(member);
          System.out.println("Member " + member + " added to role " + role);

          // Frame conditions to be set for the member (if any)
          // For more info on conditional role bindings, see:
          // https://cloud.google.com/iam/docs/managing-conditional-role-bindings?hl=en#iam-conditions-add-binding-gcloud
          Expr CEL_Expression = new Expr()
              .setExpression("resource.service == compute.googleapis.com")
              .setTitle("Compute API access")
              .setDescription(
                  "Enabling the permission for the user - limited to compute API access");

          // Bind the condition
          binding.setCondition(CEL_Expression);
          return;
        }
        System.out.println("Member is already assigned to the role ! ");
      }
    }
    System.out.println("Error ! Role not found in policy; Member not added ! ");
  }
}
