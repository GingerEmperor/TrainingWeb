// package org.example.models;
//
// import javax.persistence.Entity;
// import javax.persistence.EnumType;
// import javax.persistence.Enumerated;
// import javax.persistence.GeneratedValue;
// import javax.persistence.GenerationType;
// import javax.persistence.Id;
//
// import org.example.models.enums.RoleType;
//
// @Entity
// public class Role {
//     @Id
//     @GeneratedValue(strategy = GenerationType.AUTO)
//     private Long id;
//
//     @Enumerated(EnumType.STRING)
//     private RoleType roleType;
//
//     public Long getId() {
//         return id;
//     }
//
//     public void setId(final Long id) {
//         this.id = id;
//     }
//
//     public RoleType getRoleType() {
//         return roleType;
//     }
//
//     public void setRoleType(final RoleType roleType) {
//         this.roleType = roleType;
//     }
// }
