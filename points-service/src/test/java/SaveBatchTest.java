import com.hwq.dataloom.PointsApplication;
import com.hwq.dataloom.model.entity.UserCoupon;
import com.hwq.dataloom.service.UserCouponService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.BatchExecutorException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @author HWQ
 * @date 2024/9/2 13:58
 * @description
 */
@SpringBootTest(classes = PointsApplication.class)
@Slf4j
public class SaveBatchTest {

    @Resource
    private UserCouponService userCouponService;

    @Test
    public void saveBatch() {
        ArrayList<UserCoupon> userCoupons = new ArrayList<>();
        for (int i = 1; i < 100; i++) {
            UserCoupon userCoupon = UserCoupon.builder()
                    .couponTemplateId(1L)
                    .userId((long) i)
                    .receiveCount(1)
                    .build();
            userCoupons.add(userCoupon);
        }
        try {
            userCouponService.saveBatch(userCoupons);
        } catch (Exception e) {
            log.error("批次插入失败");
            Throwable cause = e.getCause();
            if (cause instanceof BatchExecutorException) {

            }
        }
    }
}
