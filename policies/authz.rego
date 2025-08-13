# Baheka Sentinel - Access Control Policies
# ===========================================

package baheka.rbac

import rego.v1

# Default deny all
default allow := false

# Allow health checks
allow if {
    input.path == "/actuator/health"
    input.method == "GET"
}

# Allow authentication endpoints
allow if {
    startswith(input.path, "/auth/")
}

# Allow API access for authenticated users
allow if {
    input.jwt.sub
    startswith(input.path, "/api/")
    has_permission(input.jwt, input.path, input.method)
}

# Permission checks based on JWT claims
has_permission(jwt, path, method) if {
    # Admin role has access to everything
    "admin" in jwt.realm_access.roles
}

has_permission(jwt, path, method) if {
    # Risk managers can access risk APIs
    "risk_manager" in jwt.realm_access.roles
    startswith(path, "/api/v1/risk/")
}

has_permission(jwt, path, method) if {
    # AML analysts can access AML APIs
    "aml_analyst" in jwt.realm_access.roles
    startswith(path, "/api/v1/aml/")
}

has_permission(jwt, path, method) if {
    # Compliance officers can access compliance APIs
    "compliance_officer" in jwt.realm_access.roles
    startswith(path, "/api/v1/compliance/")
}

has_permission(jwt, path, method) if {
    # Security officers can access security APIs
    "security_officer" in jwt.realm_access.roles
    startswith(path, "/api/v1/security/")
}

# Read-only access for viewers
has_permission(jwt, path, method) if {
    "viewer" in jwt.realm_access.roles
    method == "GET"
    not startswith(path, "/api/v1/admin/")
}
