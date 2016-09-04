/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sokontokoro_factory.lovelive.persistence.facade;

import lombok.Getter;
import net.sokontokoro_factory.lovelive.persistence.EntityManagerProducer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.ObjectArrayIterator;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DBの各リソースへ共通で行うCRUD操作を定義する。
 *
 * @Author fx30328
 */
public abstract class AbstractFacade<T> {
    private static final Logger logger = LogManager.getLogger(AbstractFacade.class);

    private Class<T> entityClass;
    @Getter
    private EntityManager entityManager;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 全てのレコードを取得する。
     *
     * @return
     */
    public List<T> findAll(){
        logger.info("findAll()");
        return findAll(0, 0);
    };

    /**
     * 全てのレコードを取得する。
     * offset, limitが1以上の場合、それぞれを設定したSQLが発行される。
     *
     * @return
     */
    public List<T> findAll(int offset, int limit) {
        logger.entry(offset, limit);

        // クライテリアAPIによるquery発行
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        // 検索対象のEntityを登録
        Root<T> root = query.from(entityClass);

        // select SQL文を作成
        query.select(root);
        TypedQuery<T> typedQuery = getEntityManager().createQuery(query);

        // offset, limitの設定
        if(offset > 0){
            typedQuery.setFirstResult(offset);
        }
        if(limit > 0){
            typedQuery.setMaxResults(limit);
        }

        // 実行
        return typedQuery.getResultList();
    }

    /**
     * IDに対応したEntityが存在する場合、trueを返す
     */
    public boolean isExist(Object id){
        T result = getEntityManager().find(entityClass, id);

        if(result == null){
            return false;
        }else{
            return true;
        }
    }

    /**
     * SELECT(ID検索)を実行し、entityを返却する。
     * 該当なしの場合、nullを返却する。
     *
     * @param id
     * @return 継承先Facadeクラスに対応するEntityオブジェクト。IDに対応したEntityがない場合、null
     */
    public T findById(Object id){
        logger.entry(id);
        T result = getEntityManager().find(entityClass, id);
        return logger.traceExit(result);
    }

    /**
     * entityオブジェクトをDBへ追加する
     *
     * @param entity
     * @throws EntityExistsException            一意性例外
     */
    public void create(T entity) throws EntityExistsException{
        logger.entry(entity);
        getEntityManager().persist(entity);
        logger.traceExit();
    }

    /**
     * DELETEを実行する。
     *
     * @param entity
     */
    public void delete(T entity) {
        logger.entry(entity);
        getEntityManager().remove(entityManager.merge(entity));
        logger.traceExit();
    }
}