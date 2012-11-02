package infinitealloys;

import java.io.Serializable;

public class IAWorldData implements Serializable {

	private int[] validAlloys = new int[References.validAlloyCount];

	public IAWorldData(int[] va) {
		validAlloys = va;
	}

	public int[] getValidAlloy() {
		return validAlloys;
	}
}
