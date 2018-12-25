package org.yejt.cachedemo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author keys961
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String username;

    private String password;
}
