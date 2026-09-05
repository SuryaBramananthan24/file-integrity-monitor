package com.devops.file_integrity_monitor.integrity;

import java.io.IOException;
import java.nio.file.Path;

public interface DigestService {
    String calculate(Path path) throws IOException;
}