package core.share.immutable;

import java.util.HashSet;
import java.util.Set;

// final class: 상속을 막았다.
// 상속을 이용하면 캡슐화가 깨질 수 있다.
public final class ThreeStooges {

    // stooges에 대한 참조를 변경할 수는 없다. = 참조 불변
    // 하지만 stooges 내부의 상태는 변경이 가능하다. = 완벽한 불변 객체가 아니다.
    private final Set<String> stooges = new HashSet<>();

    public ThreeStooges() {
        stooges.add("ABC");
        stooges.add("DEF");
        stooges.add("GHI");
    }

    // 만약 stooges를 변경시키 수 있는 메서드를 제공하지 않는다면 불변 객체라고 할 수도 있다.
    // 하지만 reflection을 이용해서 private인 필드에 접근할 수는 있다.
    // final: 프리미티브 타입에서는 불변, 객체 참조에서는 참조 불변!
    // 특정 객체가 불변 객체가 되려면 객체안의 참조하는 객체또한 불변이어야 한다.
    
    // stooges 내부의 상태를 변경시킨다.
    public void put(String name) {
        stooges.add(name);
    }

    public Set<String> getStooges() {
        return stooges;
    }

    public boolean isStooge(String name) {
        return stooges.contains(name);
    }
}
