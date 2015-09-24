package org.bmshackathon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class VideoImageController {

    private VideoImageRepository videoImageRepository;

    @Autowired
    public VideoImageController(VideoImageRepository videoImageRepository) {
        this.videoImageRepository = videoImageRepository;
    }

    @RequestMapping(value = "/videoImage/{videoId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VideoImage> findOne(@PathVariable Long videoId) {
        return videoImageRepository.findOne(videoId)
                .map(videoImage -> new ResponseEntity<>(videoImage, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(value = "/videoImages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public HttpEntity<Iterable<VideoImage>> findAll() {
        return new ResponseEntity<>(videoImageRepository.findAll(), OK);
    }

}
