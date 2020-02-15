package org.iii.ufo.utils;

import org.iii.utils.UriFileMapper;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class UriFileMapperTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    private UriFileMapper web2etc = new UriFileMapper(Paths.get("/www"), Paths.get("/etc"));

    @Test
    public void toLocalOK_child(){
        Path local = web2etc.toLocal(Paths.get("/www/hosts"));
        assertEquals("/etc/hosts", local.toString());
    }

    @Test
    public void toLocalOK_root(){
        Path local = web2etc.toLocal(Paths.get("/www"));
        assertEquals("/etc", local.toString());
    }

    @Test
    public void toLocalOK_root2(){
        Path local = web2etc.toLocal(Paths.get("/www/"));
        assertEquals("/etc", local.toString());
    }

    @Test(expected=RuntimeException.class)
    public void toLocalFail_notBase(){
        web2etc.toLocal(Paths.get("/www2/hosts"));
    }

    @Test
    public void toUriOK(){
        Path uri = web2etc.toUri(Paths.get("/etc/hosts"));
        assertEquals("/www/hosts", uri.toString());
    }

    @Test
    public void toUriOK_root(){
        Path uri = web2etc.toUri(Paths.get("/etc"));
        assertEquals("/www", uri.toString());
    }

    @Test
    public void toUriOK_root2(){
        Path uri = web2etc.toUri(Paths.get("/etc/"));
        assertEquals("/www", uri.toString());
    }

    @Test(expected=RuntimeException.class)
    public void toUriFail_notBase(){
        web2etc.toUri(Paths.get("/etc2/hosts"));
    }
}
