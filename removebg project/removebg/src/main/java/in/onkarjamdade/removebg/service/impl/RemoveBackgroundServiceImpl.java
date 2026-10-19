package in.onkarjamdade.removebg.service.impl;

import in.onkarjamdade.removebg.client.ClipdropClient;
import in.onkarjamdade.removebg.service.RemoveBackgoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RemoveBackgroundServiceImpl implements RemoveBackgoundService {

    @Value("${clipdrop.apikey}")
    private String apikey;

    private final ClipdropClient clipdropClient;

    @Override
    public byte [] removeBackground(MultipartFile file) {
        return clipdropClient.removeBackground(file, apikey);
    }
}
