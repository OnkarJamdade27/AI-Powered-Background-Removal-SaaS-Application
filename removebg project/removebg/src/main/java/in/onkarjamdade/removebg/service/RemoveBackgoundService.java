package in.onkarjamdade.removebg.service;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.multipart.MultipartFile;

public interface RemoveBackgoundService {

    byte [] removeBackground(MultipartFile file);
}
