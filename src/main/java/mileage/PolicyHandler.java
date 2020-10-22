package mileage;

import mileage.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler{

    @Autowired
    DormantMemberRepository DormantMemberRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverManagerMsgSent_UpdateDormantStatus(@Payload ManagerMsgSent managerMsgSent){

        if(managerMsgSent.isMe()){
            System.out.println("##### listener UpdateDormantStatus : " + managerMsgSent.toJson());

            Optional<DormantMember> memberOptional = DormantMemberRepository.findById(managerMsgSent.getMemberId());
            DormantMember DormantMember = memberOptional.get();

            if("SUCCESS".equals(managerMsgSent.getMessageStatus())){
                DormantMember.setMemberStatus("DORMANT");
            }
            else {
                DormantMember.setMemberStatus("DESTRUCTION");
            }

            DormantMemberRepository.save(DormantMember);


        }
    }

}
