package ru.kataaas.kaflent.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberDTO {

    private String username;

    private boolean isAdmin;

    private boolean applicationAccepted;

}
