package memoapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import memoapp.dto.BulkPriorityUpdateRequest;
import memoapp.dto.PriorityStatistics;
import memoapp.dto.PriorityUpdateRequest;
import memoapp.entity.Memo;
import memoapp.entity.Priority;
import memoapp.service.MemoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * REST controller for memo operations.
 * 
 * Updated to work with the refactored MemoService that follows SOLID principles.
 * The service now throws meaningful exceptions instead of returning null values,
 * so exception handling is managed by Spring's global exception handler.
 */
@Tag(name = "Memo Management", description = "Operations for managing memos and notes")
@RestController
@RequestMapping("/api/memos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MemoController {

    private final MemoService memoService;

    /**
     * Constructor injection following Dependency Inversion Principle.
     * Replaced @Autowired field injection with constructor injection for better testability.
     */
    public MemoController(MemoService memoService) {
        this.memoService = memoService;
    }

    @Operation(summary = "Get all memos", description = "Retrieve all memos with optional filtering by priority and sorting")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved memos",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Memo.class)))
    })
    @GetMapping
    public List<Memo> getAllMemos(
            @Parameter(description = "Filter by priority (comma-separated values: LOW, MEDIUM, HIGH)")
            @RequestParam(required = false) String priority,
            @Parameter(description = "Sort order: asc or desc")
            @RequestParam(required = false) String sort) {
        
        // Handle priority filtering
        if (priority != null && !priority.isEmpty()) {
            List<Priority> priorities = Arrays.stream(priority.split(","))
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .map(Priority::fromValue)
                    .collect(java.util.stream.Collectors.toList());
            return memoService.getMemosByPriority(priorities);
        }
        
        // Handle sorting
        if (sort != null && !sort.isEmpty()) {
            return memoService.getMemosSortedByPriority(sort);
        }
        
        return memoService.getAllMemos();
    }

    @Operation(summary = "Get memo by ID", description = "Retrieve a specific memo by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved memo",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Memo.class))),
            @ApiResponse(responseCode = "404", description = "Memo not found")
    })
    @GetMapping("/{id}")
    public Memo getMemoById(@Parameter(description = "ID of the memo to retrieve") @PathVariable Long id) {
        return memoService.getMemoById(id);
    }

    @Operation(summary = "Create a new memo", description = "Create a new memo with title and content")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Memo created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Memo.class))),
            @ApiResponse(responseCode = "400", description = "Invalid memo data")
    })
    @PostMapping
    public Memo createMemo(@Parameter(description = "Memo data") @RequestBody Memo memo) {
        return memoService.createMemo(memo);
    }

    @Operation(summary = "Update a memo", description = "Update an existing memo by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Memo updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Memo.class))),
            @ApiResponse(responseCode = "404", description = "Memo not found"),
            @ApiResponse(responseCode = "400", description = "Invalid memo data")
    })
    @PutMapping("/{id}")
    public Memo updateMemo(@Parameter(description = "ID of the memo to update") @PathVariable Long id, 
                          @Parameter(description = "Updated memo data") @RequestBody Memo memo) {
        return memoService.updateMemo(id, memo);
    }

    @Operation(summary = "Delete a memo", description = "Delete a memo by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Memo deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Memo not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemo(@Parameter(description = "ID of the memo to delete") @PathVariable Long id) {
        memoService.deleteMemo(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(summary = "Update memo priority", description = "Update the priority of a specific memo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Priority updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Memo.class))),
            @ApiResponse(responseCode = "404", description = "Memo not found"),
            @ApiResponse(responseCode = "400", description = "Invalid priority data")
    })
    @PutMapping("/{id}/priority")
    public Memo updateMemoPriority(@Parameter(description = "ID of the memo to update") @PathVariable Long id, 
                                  @Parameter(description = "Priority update request") @Valid @RequestBody PriorityUpdateRequest request) {
        return memoService.updateMemoPriority(id, request.getPriority());
    }
    
    @Operation(summary = "Bulk update memo priorities", description = "Update priority for multiple memos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Priorities updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Memo.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/bulk/priority")
    public List<Memo> bulkUpdatePriority(@Parameter(description = "Bulk priority update request") @Valid @RequestBody BulkPriorityUpdateRequest request) {
        return memoService.bulkUpdatePriority(request);
    }
    
    @Operation(summary = "Get priority statistics", description = "Get statistics about memo priorities")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PriorityStatistics.class)))
    })
    @GetMapping("/stats/priority")
    public PriorityStatistics getPriorityStatistics() {
        return memoService.getPriorityStatistics();
    }
}