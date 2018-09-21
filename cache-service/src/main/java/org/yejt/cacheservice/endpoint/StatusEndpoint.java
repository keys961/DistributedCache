package org.yejt.cacheservice.endpoint;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StatusEndpoint
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusEndpoint.class);

    @GetMapping(value = "/health")
    public ResponseEntity health()
    {
        try
        {
            InstanceInfo myInfo = ApplicationInfoManager.getInstance().getInfo();
            switch(myInfo.getStatus())
            {
                case UP:
                    return ResponseEntity.ok().build();
                case STARTING:
                    return ResponseEntity.noContent().build();
                case OUT_OF_SERVICE:
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        catch (Throwable var2)
        {
            LOGGER.error("Error when doing health check.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
