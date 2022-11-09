package reference.phantom;

import reference.Team;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class TeamReference extends PhantomReference<Team> {
     
    public TeamReference(Team referent, ReferenceQueue<? super Team> q) {
        super(referent, q);
    }

    @Override
    public void clear() {
        super.clear();
        // reference 후처리
        System.out.println("clearing...");
    }
}
