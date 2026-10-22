package com.bumbumapps.bouncy.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.junit.Test;

/**
 * Regression: with libGDX 1.12+ Box2D needs libgdx.so (BufferUtils); without the core natives the
 * game crashed on launch with UnsatisfiedLinkError.
 */
public class NativesConfigTest {
    @Test
    public void buildShipsGdxCoreNativesForEveryAbi() throws Exception {
        File f = new File("build.gradle");
        if (!f.exists()) f = new File("app/build.gradle");
        String gradle = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
        for (String abi : new String[]{"armeabi-v7a", "arm64-v8a", "x86", "x86_64"}) {
            assertTrue(abi, gradle.contains("gdx-platform:$gdxVersion:natives-" + abi));
        }
    }
}
