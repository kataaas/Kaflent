package ru.kataaas.kaflent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import ru.kataaas.kaflent.repository.GroupRepository;
import ru.kataaas.kaflent.repository.GroupUserJoinRepository;
import ru.kataaas.kaflent.service.GroupUserJoinService;

@SpringBootTest
public class GroupServiceTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupUserJoinRepository groupUserJoinRepository;

    @Test
    public void test() {
        groupRepository.deleteById(18L);

    }



}
