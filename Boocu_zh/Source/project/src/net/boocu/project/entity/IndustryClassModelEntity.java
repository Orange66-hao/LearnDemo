
package net.boocu.project.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.boocu.framework.entity.BaseEntity;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


/**
 * 一个instrumentModel对应多个industryClassEntity
 * author fang
 * 
 * 20150827
 * version 1.0
 * 
 * */



@Entity
@Table(name="jhj_industryClassModel")
public class IndustryClassModelEntity extends BaseEntity {
    private InstrumentModelEntity instrumentModelEntity;

    private IndustryClassEntity industryClassEntity;
    @OneToOne()
    @JoinColumn(name="industry_id")
    @NotFound(action=NotFoundAction.IGNORE)
    public IndustryClassEntity getIndustryClassEntity() {
        return industryClassEntity;
    }

    public void setIndustryClassEntity(IndustryClassEntity industryClassEntity) {
        this.industryClassEntity = industryClassEntity;
    }
    @ManyToOne(cascade = {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH},optional=false)
    // JoinColumn表示外键的列
    @JoinColumn(name="instrumentModel_id")
    public InstrumentModelEntity getInstrumentModelEntity() {
        return instrumentModelEntity;
    }

    public void setInstrumentModelEntity(InstrumentModelEntity instrumentModelEntity) {
        this.instrumentModelEntity = instrumentModelEntity;
    }
}
