package org.springframework.samples.petclinic.owner;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.style.ToStringCreator;
import org.springframework.samples.petclinic.model.Person;
import org.springframework.util.Assert;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;

@Entity //Entityクラス
@Table(name = "owners") //エンティティにマッピングされる物理テーブル名を指定
public class Owner extends Person {

	@Column(name = "address")
	@NotEmpty
	private String address;

	@Column(name = "city")
	@NotEmpty
	private String city;

	@Column(name = "telephone")
	@NotEmpty
	@Digits(fraction = 0, integer = 10)
	private String telephone;

	//owner_idが一致するPetテーブルを全て取得
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) //全ての操作をカスケード、即座取得
	@JoinColumn(name = "owner_id") //owner_idをもとにテーブル結合
	@OrderBy("name") //nameを昇順ソート
	private List<Pet> pets = new ArrayList<>();

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public List<Pet> getPets() {
		return this.pets;
	}

	public void addPet(Pet pet) {
		//引数のpetクラスのidがnullの場合、petリストに要素を追加
		if (pet.isNew()) {
			getPets().add(pet);
		}
	}

	/**
	 * String引数のみの場合、無条件でnameが同値のものを取得する
	 */
	public Pet getPet(String name) {
		return getPet(name, false);
	}

	/**
	 * idでPetクラスを取得
	 */
	public Pet getPet(Integer id) {
		for (Pet pet : getPets()) {
			if (!pet.isNew()) {
				Integer compId = pet.getId();
				if (compId.equals(id)) {
					return pet;
				}
			}
		}
		return null;
	}

	public Pet getPet(String name, boolean ignoreNew) {
		name = name.toLowerCase();//大文字を全て小文字に変換
		for (Pet pet : getPets()) {
			String compName = pet.getName();
			if (compName != null && compName.equalsIgnoreCase(name)) {//大文字小文字区別せず同値判定を行う
				if (!ignoreNew || !pet.isNew()) {//ignoreNewがfalseもしくpet.isNewがfalseの場合、そのPetクラスを返す
					return pet;
				}
			}
		}
		return null;
	}

	/**
	 *toStriingのオーバーライド
	 */
	@Override
	public String toString() {
		return new ToStringCreator(this).append("id", this.getId())
				.append("new", this.isNew())
				.append("lastName", this.getLastName())
				.append("firstName", this.getFirstName())
				.append("address", this.address)
				.append("city", this.city)
				.append("telephone", this.telephone)
				.toString();
	}

	public void addVisit(Integer petId, Visit visit) {
		Assert.notNull(petId, "Pet identifier must not be null!");//nullの場合エクセプションを発生させる
		Assert.notNull(visit, "Visit must not be null!");//nullの場合エクセプションを発生させる

		Pet pet = getPet(petId);

		Assert.notNull(pet, "Invalid Pet identifier!");//nullの場合エクセプションを発生させる

		pet.addVisit(visit);
	}

}
