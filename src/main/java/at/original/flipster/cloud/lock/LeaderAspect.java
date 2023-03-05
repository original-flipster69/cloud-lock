package at.original.flipster.cloud.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LeaderAspect {

    @Around("@annotation(Leader)")
    public Object limitToLeader(ProceedingJoinPoint joinPoint, Leader leader) throws Throwable {
        Storage storage = null;
        if(leader.vendor() == Vendor.GCP) {
            storage = new GoogleCloudStorage("tylerlockett", "leader.txt");
        } else if (leader.vendor() == Vendor.AZURE) {
            storage = new AzureBlobStorage("tylerlockett", "leader.txt");
        }
        CloudLock lock = new CloudLock(storage);
        boolean acquiredLock = lock.acquireLock();
        if (!acquiredLock) {
            return null;
        }
        var result =  joinPoint.proceed();
        lock.releaseLock();
        return result;
    }
}
