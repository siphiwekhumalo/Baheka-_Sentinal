-- Initialize Baheka Sentinel Database
-- This script sets up the core database structure for all modules

-- Create schemas for different modules
CREATE SCHEMA IF NOT EXISTS sentinel_risk;
CREATE SCHEMA IF NOT EXISTS sentinel_aml;
CREATE SCHEMA IF NOT EXISTS sentinel_compliance;
CREATE SCHEMA IF NOT EXISTS sentinel_secure;
CREATE SCHEMA IF NOT EXISTS sentinel_core;

-- Enable TimescaleDB extension for time-series data
CREATE EXTENSION IF NOT EXISTS timescaledb;
CREATE EXTENSION IF NOT EXISTS uuid_ossp;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Core entities
CREATE TABLE sentinel_core.organizations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    country_code VARCHAR(3) NOT NULL,
    license_number VARCHAR(100),
    regulatory_body VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sentinel_core.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Risk Module Tables
CREATE TABLE sentinel_risk.risk_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    customer_id VARCHAR(100) NOT NULL,
    profile_type VARCHAR(50) NOT NULL, -- 'INDIVIDUAL', 'CORPORATE'
    risk_score DECIMAL(5,2) NOT NULL,
    risk_category VARCHAR(20) NOT NULL, -- 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'
    pd_score DECIMAL(8,6), -- Probability of Default
    lgd_score DECIMAL(8,6), -- Loss Given Default
    ead_amount DECIMAL(18,2), -- Exposure at Default
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sentinel_risk.risk_metrics (
    time TIMESTAMPTZ NOT NULL,
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    metric_type VARCHAR(50) NOT NULL, -- 'VaR', 'CVaR', 'CAPITAL_RATIO'
    metric_value DECIMAL(18,6) NOT NULL,
    currency VARCHAR(3) DEFAULT 'USD',
    confidence_level DECIMAL(5,2),
    time_horizon INTEGER, -- days
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Convert to hypertable for time-series optimization
SELECT create_hypertable('sentinel_risk.risk_metrics', 'time');

-- AML Module Tables
CREATE TABLE sentinel_aml.transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    transaction_id VARCHAR(100) NOT NULL,
    amount DECIMAL(18,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    sender_account VARCHAR(100) NOT NULL,
    receiver_account VARCHAR(100) NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,
    channel VARCHAR(50) NOT NULL, -- 'ATM', 'ONLINE', 'BRANCH', 'MOBILE'
    country_code VARCHAR(3),
    processed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sentinel_aml.alerts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    transaction_id UUID REFERENCES sentinel_aml.transactions(id),
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL, -- 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'
    status VARCHAR(20) DEFAULT 'OPEN', -- 'OPEN', 'INVESTIGATING', 'CLOSED', 'ESCALATED'
    assigned_to UUID REFERENCES sentinel_core.users(id),
    rule_triggered VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sentinel_aml.watchlist_matches (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    entity_name VARCHAR(255) NOT NULL,
    entity_type VARCHAR(50) NOT NULL, -- 'INDIVIDUAL', 'ORGANIZATION'
    watchlist_source VARCHAR(100) NOT NULL, -- 'OFAC', 'UN', 'EU', 'FIC_SA'
    match_score DECIMAL(5,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- 'PENDING', 'CONFIRMED', 'FALSE_POSITIVE'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Compliance Module Tables
CREATE TABLE sentinel_compliance.regulations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    jurisdiction VARCHAR(100) NOT NULL,
    effective_date DATE NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sentinel_compliance.reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    regulation_id UUID REFERENCES sentinel_compliance.regulations(id),
    report_type VARCHAR(100) NOT NULL,
    report_period_start DATE NOT NULL,
    report_period_end DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'DRAFT', -- 'DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED'
    submitted_by UUID REFERENCES sentinel_core.users(id),
    submitted_at TIMESTAMP,
    file_path VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sentinel_compliance.audit_trails (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    user_id UUID REFERENCES sentinel_core.users(id),
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    timestamp TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Security Module Tables
CREATE TABLE sentinel_secure.access_policies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    policy_name VARCHAR(255) NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_pattern VARCHAR(255) NOT NULL,
    allowed_actions TEXT[] NOT NULL,
    conditions JSONB,
    is_active BOOLEAN DEFAULT true,
    created_by UUID REFERENCES sentinel_core.users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sentinel_secure.security_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    organization_id UUID REFERENCES sentinel_core.organizations(id),
    user_id UUID REFERENCES sentinel_core.users(id),
    event_type VARCHAR(50) NOT NULL, -- 'LOGIN', 'LOGOUT', 'FAILED_LOGIN', 'PRIVILEGE_ESCALATION'
    severity VARCHAR(20) NOT NULL,
    source_ip INET NOT NULL,
    user_agent TEXT,
    details JSONB,
    timestamp TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_risk_profiles_org_customer ON sentinel_risk.risk_profiles(organization_id, customer_id);
CREATE INDEX idx_risk_metrics_time_org ON sentinel_risk.risk_metrics(time, organization_id);
CREATE INDEX idx_transactions_org_processed ON sentinel_aml.transactions(organization_id, processed_at);
CREATE INDEX idx_alerts_org_status ON sentinel_aml.alerts(organization_id, status);
CREATE INDEX idx_audit_trails_org_timestamp ON sentinel_compliance.audit_trails(organization_id, timestamp);
CREATE INDEX idx_security_events_org_timestamp ON sentinel_secure.security_events(organization_id, timestamp);

-- Sample data
INSERT INTO sentinel_core.organizations (name, code, country_code, license_number, regulatory_body) VALUES
('Standard Bank', 'SBK', 'ZAF', 'SB001', 'SARB'),
('First National Bank', 'FNB', 'ZAF', 'FNB001', 'SARB'),
('Absa Bank', 'ABSA', 'ZAF', 'ABSA001', 'SARB');

INSERT INTO sentinel_compliance.regulations (name, code, jurisdiction, effective_date, description) VALUES
('Basel III Capital Requirements', 'BASEL_III', 'GLOBAL', '2019-01-01', 'International regulatory framework for banks'),
('Financial Intelligence Centre Act', 'FICA', 'ZAF', '2001-12-01', 'South African AML/CFT legislation'),
('Protection of Personal Information Act', 'POPIA', 'ZAF', '2021-07-01', 'South African data protection regulation'),
('Bank Secrecy Act', 'BSA', 'USA', '1970-10-26', 'US AML legislation'),
('Anti-Money Laundering Directive', 'AMLD', 'EU', '2018-01-10', 'European Union AML framework');
