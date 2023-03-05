package at.original.flipster.cloud.lock;

//@Aspect
//@Component
public class LeaderAspect {

//    @Around("@annotation(Leader)")
//    public Object limitToLeader(ProceedingJoinPoint joinPoint, Leader leader) throws Throwable {
//        new LeaderOnlyExecution().onlyExecuteAsLeader(() -> {
//            try {
//                joinPoint.proceed();
//            } catch (Throwable e) {
//                throw new RuntimeException(e);
//            }
//        }, leader.vendor());
//        return null;
//    }
}
