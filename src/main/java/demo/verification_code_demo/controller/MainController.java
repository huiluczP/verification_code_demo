package demo.verification_code_demo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.verification_code_demo.bean.VerificationCodePlace;
import demo.verification_code_demo.util.VerificationCodeAdapter;
import oracle.jrockit.jfr.StringConstantPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController{

    @Value("${afterImage.location}")
    private String imgLocation;

    @RequestMapping("/index")
    public String index(){
        return "index.html";
    }

    @RequestMapping("/getImgInfo")
    @ResponseBody
    // 随机获取背景和拼图，返回json
    public String imgInfo(){
        VerificationCodePlace vcPlace =VerificationCodeAdapter.getRandomVerificationCodePlace(imgLocation);
        ObjectMapper om = new ObjectMapper();
        String jsonResult = "";
        try {
            jsonResult = om.writeValueAsString(vcPlace);
            return jsonResult;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    @RequestMapping("/vcode")
    // 验证码主界面
    public String vCode(){
        return "vc_sample.html";
    }

    @RequestMapping("/deleteImg")
    @ResponseBody
    // 删除生成的验证码图片
    public String deleteImg(){
        return VerificationCodeAdapter.deleteAfterImage(imgLocation);
    }
}
