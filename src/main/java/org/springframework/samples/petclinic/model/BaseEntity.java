package org.springframework.samples.petclinic.model;

import java.io.Serializable;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * @author sotatk25
 * MappedSuperclassは、共通で扱うメンバー変数を定義したいときに使うアノテーション
 * idは共通で扱うので、BaseEntityクラス内に定義
 *
 */
@MappedSuperclass
public class BaseEntity implements Serializable {

	@Id //主キーを設定
	@GeneratedValue(strategy = GenerationType.IDENTITY) //主キーの生成をDBに委任する
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * idがnullかどうか(新規登録されたものかどうか)判定させるメソッド
	 * 
	 * @return
	 */
	public boolean isNew() {
		return this.id == null;
	}

}
