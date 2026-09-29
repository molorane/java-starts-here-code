package za.co.molorane.javastartshere.ch10;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Chapter 10: Enterprise Toolkit Manifest Verification")
class ToolkitManifestTest {

    @Test
    @DisplayName("Should verify presence of multi-stage Dockerfile and Kubernetes manifests")
    void shouldVerifyDeploymentManifests() {
        File dockerfile = new File("Dockerfile");
        File k8s = new File("k8s/deployment.yaml");

        assertThat(dockerfile).exists();
        assertThat(k8s).exists();
    }
}
