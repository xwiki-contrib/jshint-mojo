package com.cj.jshintmojo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import static com.cj.jshintmojo.util.Util.deleteDirectory;
import static com.cj.jshintmojo.util.Util.mkdirs;
import static com.cj.jshintmojo.util.Util.tempDir;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MojoTest {
    @Test
    void walksTheDirectoryTreeToFindAndUseJshintFiles() throws Exception {
        String[] jshintIgnorePaths = {".jshintignore", "foo/.jshintignore", "foo/bar/.jshintignore", "foo/bar/baz/.jshintignore"};
        
        for(String jshintIgnorePath: jshintIgnorePaths){
            File directory = tempDir();
            try{
                // given
                File ignoreFile = new File(directory, jshintIgnorePath);
                FileUtils.writeStringToFile(ignoreFile, "src/main/resources/foo.qunit.js", StandardCharsets.UTF_8);
                
                File projectDirectory = mkdirs(directory, "foo/bar/baz");
                File resourcesDirectory = mkdirs(projectDirectory, "src/main/resources");
                
                File fileToIgnore = new File(resourcesDirectory, "foo.qunit.js");
                FileUtils.writeStringToFile(fileToIgnore, "whatever, this should be ignored", StandardCharsets.UTF_8);
                
                LogStub log = new LogStub();
                Mojo mojo = new Mojo("", "", 
                        projectDirectory, 
                        Collections.singletonList("src/main/resources"), 
                        Collections.<String>emptyList(),true, null, null, null, null);
                mojo.setLog(log);
                
                // when
                mojo.execute();
                
                // then
                assertTrue(log.hasMessage("info", "Using ignore file: " + ignoreFile.getAbsolutePath()), "Sees ignore files");
                assertTrue(log.hasMessage("warn", "Excluding " + fileToIgnore.getAbsolutePath()), "Uses ignore files");
                
            }finally{
                deleteDirectory(directory);
            }
        }
    }
    
	@Test
	void warnsUsersWhenConfiguredToWorkWithNonexistentDirectories() throws Exception {
		File directory = tempDir();
		try{
			// given
			mkdirs(directory, "src/main/resources");
			LogStub log = new LogStub();
			Mojo mojo = new Mojo("", "", 
							directory, 
							Collections.singletonList("src/main/resources/nonexistentDirectory"), 
							Collections.<String>emptyList(),true, null, null, null, null);
			mojo.setLog(log);

			// when
			mojo.execute();

			// then
			assertEquals(1, log.messagesForLevel("warn").size());
			assertEquals("You told me to find tests in src/main/resources/nonexistentDirectory, but there is nothing there (" + directory.getAbsolutePath() + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "nonexistentDirectory)",
								log.messagesForLevel("warn").get(0).content.toString());


		}finally{
			deleteDirectory(directory);
		}
	}
	
	@Test
	void resolvesConfigFileRelativeToMavenBasedirProperty() throws Exception {
		File directory = tempDir();
		try{
			// given
			mkdirs(directory, "src/main/resources");
			mkdirs(directory, "foo/bar");
			FileUtils.writeLines(new File(directory, "foo/bar/my-config-file.js"), Arrays.asList(
					"{",
					"  \"globals\": {", 
					"    \"require\": false",
					"  }",     
					"}"
					));
			
			Mojo mojo = new Mojo(null, "", 
							directory, 
							Collections.singletonList("src/main/resources/"), 
							Collections.<String>emptyList(),true, "foo/bar/my-config-file.js", null, null, null);
			
			LogStub log = new LogStub();
			mojo.setLog(log);

			// when
			mojo.execute();
			
			// then
			final String properPathForConfigFile = new File(directory, "foo/bar/my-config-file.js").getAbsolutePath();
			assertTrue(log.hasMessage("info", "Using configuration file: " + properPathForConfigFile));
			
		}finally{
			deleteDirectory(directory);
		}
	}
}
