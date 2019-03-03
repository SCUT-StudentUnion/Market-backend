package org.scutsu.market.models;

public class Views {
	public interface Minimum {
	}

	public interface Goods {
		interface Public extends Minimum {
		}

		interface List extends Public {
		}

		interface Detail extends Public {
		}

		interface Admin extends Public {
		}

		interface UserAccessible {
		}
	}
}
