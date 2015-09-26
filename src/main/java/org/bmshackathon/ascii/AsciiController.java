package org.bmshackathon.ascii;

import org.apache.commons.io.FileUtils;
import org.bmshackathon.VideoImageRepository;
import org.roqe.jitac.JitacException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.GET;


@Controller
public class AsciiController {

    private final VideoImageRepository repository;

    @Autowired
    public AsciiController(VideoImageRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "ascii/{id}", method = GET)
    @ResponseBody
    public FileSystemResource getFile(@PathVariable("id") long id) throws IOException, InterruptedException, JitacException {

        String tempDir = System.getProperty("java.io.tmpdir");
        URL imageUrl = new URL(repository.findOne(id).get().getImageUrl());
        String fileName = UUID.randomUUID()+".jpg";
        File destinationFile = new File(tempDir, fileName);
        FileUtils.copyURLToFile(imageUrl, destinationFile);

        String generatedHtmlPath = UUID.randomUUID()+".html";

        String generatedAsciiFile = tempDir+"/"+generatedHtmlPath;
        String[] args = {destinationFile.getAbsolutePath(), "-C", "-o", generatedAsciiFile};
        Jitac.start(args);

        return new FileSystemResource(generatedAsciiFile);
    }
}
