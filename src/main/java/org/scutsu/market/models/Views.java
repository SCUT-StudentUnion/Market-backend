package org.scutsu.market.models;

/**
 * 用于{@link com.fasterxml.jackson.annotation.JsonView}
 */
public class Views {
	public interface Minimum {
	}

	public interface Goods {
		/**
		 * 在所有公开展示中都需要的信息
		 */
		interface Public extends Minimum {
		}

		/**
		 * 在对公众展示的列表中所需信息
		 */
		interface List extends Public {
		}

		/**
		 * 在对公众展示的详细信息界面所需信息
		 */
		interface Detail extends Public {
		}

		/**
		 * 商品发布者自己可见信息
		 */
		interface Self extends Public {
		}

		/**
		 * 管理员可见和可修改的信息
		 */
		interface Admin extends Self {
		}

		/**
		 * 商品发布者可以更改的信息
		 */
		interface UserAccessible {
		}
	}
}
