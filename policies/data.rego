# Baheka Sentinel - Data Access Policies
# =======================================

package baheka.data

import rego.v1

# Default deny
default allow := false

# Organization-level data isolation
allow if {
    input.resource.organization_id == input.jwt.organization_id
    is_authorized_for_resource(input.jwt, input.resource.type, input.action)
}

# Super admin can access all organizations
allow if {
    "super_admin" in input.jwt.realm_access.roles
}

# Resource-level authorization
is_authorized_for_resource(jwt, resource_type, action) if {
    resource_type == "risk_profile"
    "risk_manager" in jwt.realm_access.roles
}

is_authorized_for_resource(jwt, resource_type, action) if {
    resource_type == "aml_alert"
    "aml_analyst" in jwt.realm_access.roles
}

is_authorized_for_resource(jwt, resource_type, action) if {
    resource_type == "compliance_report"
    "compliance_officer" in jwt.realm_access.roles
}

# Sensitive data masking rules
mask_fields contains field if {
    input.resource.type == "customer"
    field := "ssn"
    not "admin" in input.jwt.realm_access.roles
}

mask_fields contains field if {
    input.resource.type == "transaction"
    field := "account_number"
    not has_pii_access(input.jwt)
}

has_pii_access(jwt) if {
    "pii_access" in jwt.realm_access.roles
}

has_pii_access(jwt) if {
    "admin" in jwt.realm_access.roles
}
