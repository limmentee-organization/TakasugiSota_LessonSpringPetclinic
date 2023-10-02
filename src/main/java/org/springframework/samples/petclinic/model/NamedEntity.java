package org.springframework.samples.petclinic.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * 
 * @author sotatk25
 *nameを宣言するクラス
 *BaseEntityを継承することで変数idを使用できる
 */
@MappedSuperclass
public class NamedEntity extends BaseEntity {

	@Column(name = "name") //DBのカラム名と紐付け
	private String name;

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 *toStringのオーバーライド
	 */
	@Override
	public String toString() {
		return this.getName();
	}

}
