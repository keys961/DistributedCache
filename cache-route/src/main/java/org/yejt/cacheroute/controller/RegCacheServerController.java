package org.yejt.cacheroute.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController("/reg")
public class RegCacheServerController
{
    // TODO: Reg/Unreg cache server
    public ResponseEntity regCacheServer()
    {
        return ResponseEntity.ok().build();
    }

    public ResponseEntity unregCacheServer()
    {
        return ResponseEntity.ok().build();
    }
}
