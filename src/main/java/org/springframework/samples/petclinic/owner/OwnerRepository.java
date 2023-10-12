package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface OwnerRepository extends Repository<Owner, Integer> {

	//全件取得
	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	@Transactional(readOnly = true)
	List<PetType> findPetTypes();

	//ページネーション(lastnameで検索)
	@Query("SELECT DISTINCT owner FROM Owner owner left join  owner.pets WHERE owner.lastName LIKE :lastName%")
	@Transactional(readOnly = true)
	Page<Owner> findByLastName(@Param("lastName") String lastName, Pageable pageable);

	//idで1件取得
	@Query("SELECT owner FROM Owner owner left join fetch owner.pets WHERE owner.id=:id")
	@Transactional(readOnly = true)
	Owner findById(@Param("id") Integer id);

	//登録用メソッド？アノテーションがなくてもsaveメソッドを宣言すればオーバーライドされる？
	void save(Owner owner);

	//ページネーション(無条件)
	@Query("SELECT owner FROM Owner owner")
	@Transactional(readOnly = true)
	Page<Owner> findAll(Pageable pageable);

}
