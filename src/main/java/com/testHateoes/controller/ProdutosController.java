package com.testHateoes.controller;

import com.testHateoes.dto.ProdutosDto;
import com.testHateoes.entity.ProdutosEntity;
import com.testHateoes.repository.ProdutosRepository;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping ("/api/produtos")
public class ProdutosController {

    @Autowired
    private ProdutosRepository repository;

    @GetMapping
    public ResponseEntity<List<ProdutosEntity>> pegaTodosProdutos() {
        List<ProdutosEntity> listaprodutos = repository.findAll();
        if (listaprodutos.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            for (ProdutosEntity produto : listaprodutos){
                long id = produto.getId();
                    produto.add(linkTo(methodOn(ProdutosController.class).pegaUmProduto(id)).withSelfRel());
                }
            }
            return new ResponseEntity<List<ProdutosEntity>>(listaprodutos, HttpStatus.OK);
        }


    @GetMapping ("/api/produtos/{id}")
    public ResponseEntity<ProdutosEntity> pegaUmProduto(@PathVariable (value = "id")long id){
        Optional<ProdutosEntity> produtoO = repository.findById(id);
        if (!produtoO.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            produtoO.get().add(linkTo(methodOn(ProdutosController.class).pegaTodosProdutos()).withRel("Lista de produtos"));
            return new ResponseEntity<>(produtoO.get(),HttpStatus.OK);
        }
    }


    @PostMapping
    public ResponseEntity<ProdutosEntity> cadastrarProduto(@RequestBody ProdutosDto dto){
        var ProdutosEntity = new ProdutosEntity();
        BeanUtils.copyProperties(dto ,ProdutosEntity);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(ProdutosEntity));

    }
}


