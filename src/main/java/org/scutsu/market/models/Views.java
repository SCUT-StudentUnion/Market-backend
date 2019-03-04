package org.scutsu.market.models;

/**
 * 用于{@link com.fasterxml.jackson.annotation.JsonView}
 */
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
