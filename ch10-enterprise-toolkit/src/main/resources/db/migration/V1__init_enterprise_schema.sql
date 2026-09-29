CREATE TABLE cluster_nodes (
    id VARCHAR(64) PRIMARY KEY,
    hostname VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    registered_at TIMESTAMP NOT NULL
);

CREATE TABLE deployment_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(128) NOT NULL,
    version VARCHAR(32) NOT NULL,
    deployed_at TIMESTAMP NOT NULL,
    status VARCHAR(32) NOT NULL
);
