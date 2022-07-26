package ru.kataaas.kaflent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kataaas.kaflent.repository.GroupRepository;
import ru.kataaas.kaflent.repository.GroupUserJoinRepository;
import ru.kataaas.kaflent.service.GroupService;
import ru.kataaas.kaflent.service.GroupUserJoinService;

@SpringBootTest
public class GroupServiceTest {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupUserJoinRepository groupUserJoinRepository;

    @Test
    @Transactional
    public void test() {
        groupService.createGroup(2L, "da","PUBLIC");
    }



}
