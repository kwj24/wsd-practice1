package com.example.ksl.wsd2025.api.v4;

import com.example.ksl.wsd2025.api.dto.ItemDto;
import com.example.ksl.wsd2025.api.request.ItemCreateRequest;
import com.example.ksl.wsd2025.api.request.ItemUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v4/items")
public class ItemController4 {

    // 메모리용 임시 DB (예제용)
    private final Map<Long, ItemDto> store = new HashMap<>();
    private long sequence = 1L;

    // 1) GET: 전체 조회
    @GetMapping
    public List<ItemDto> getItems() {
        return new ArrayList<>(store.values());
    }

    // 1) GET: 단건 조회 (PathVariable 사용)
    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long id) {
        ItemDto item = store.get(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(item);
    }

    // 1) POST: 생성 (201 Created / 이름이나 가격이 없으면 400)
    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestBody ItemCreateRequest request) {
        if (request.getName() == null || request.getName().isBlank() || request.getPrice() == null) {
            return ResponseEntity.badRequest().build();
        }

        ItemDto item = new ItemDto();
        item.setId(sequence++);
        item.setName(request.getName());
        item.setPrice(request.getPrice());

        store.put(item.getId(), item);

        return ResponseEntity.status(201).body(item);
    }

    // 1) PUT: 수정
    @PutMapping("/{id}")
    public ResponseEntity<ItemDto> updateItem(
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemDto item = store.get(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.getName() != null) {
            item.setName(request.getName());
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }

        return ResponseEntity.ok(item);
    }

    // 2) PUT: 가격만 수정 (price가 없거나 음수면 400)
    @PutMapping("/{id}/price")
    public ResponseEntity<ItemDto> updatePrice(
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request
    ) {
        ItemDto item = store.get(id);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        if (request.getPrice() == null || request.getPrice() < 0) {
            return ResponseEntity.badRequest().build();
        }

        item.setPrice(request.getPrice());

        return ResponseEntity.ok(item);
    }

    // 1) DELETE: 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        ItemDto removed = store.remove(id);
        if (removed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    // 2) DELETE: 전체 삭제
    @DeleteMapping
    public ResponseEntity<Void> deleteAllItems() {
        store.clear();
        return ResponseEntity.noContent().build();
    }

    // /api/v4/items/search?keyword=abc&page=0&size=10
    @GetMapping("/search")
    public List<ItemDto> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // 예제: keyword 포함된 name만 필터링
        return store.values().stream()
                .filter(item -> keyword == null || item.getName().contains(keyword))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    // 2) POST: 헤더 사용
    @PostMapping("/with-header")
    public ResponseEntity<String> createItemWithHeader(
            @RequestBody ItemCreateRequest request,
            @RequestHeader(value = "X-USER-ID", required = false) String userId,
            @RequestHeader(value = "Authorization", required = false) String authorization
    ) {
        StringBuilder sb = new StringBuilder();
        sb.append("요청한 아이템: ").append(request.getName())
                .append(", price=").append(request.getPrice()).append("\n")
                .append("X-USER-ID: ").append(userId).append("\n")
                .append("Authorization: ").append(authorization);

        return ResponseEntity.ok(sb.toString());
    }

    // 테스트용: 500 응답
    @GetMapping("/debug/error")
    public ResponseEntity<Void> debugError() {
        return ResponseEntity.status(500).build();
    }

    // 테스트용: 503 응답
    @GetMapping("/debug/unavailable")
    public ResponseEntity<Void> debugUnavailable() {
        return ResponseEntity.status(503).build();
    }
}