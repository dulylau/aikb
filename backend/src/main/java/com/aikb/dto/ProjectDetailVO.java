package com.aikb.dto;

import com.aikb.entity.Product;
import com.aikb.entity.Project;
import com.aikb.entity.ProjectMember;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDetailVO {

    private Long id;
    private String name;
    private String code;
    private String description;
    private int productCount;
    private List<Product> products;
    private List<ProjectMember> members;

    public static ProjectDetailVO from(Project project, List<Product> products, List<ProjectMember> members) {
        ProjectDetailVO vo = new ProjectDetailVO();
        vo.setId(project.getId());
        vo.setName(project.getName());
        vo.setCode(project.getCode());
        vo.setDescription(project.getDescription());
        vo.setProductCount(products.size());
        vo.setProducts(products);
        vo.setMembers(members);
        return vo;
    }
}
