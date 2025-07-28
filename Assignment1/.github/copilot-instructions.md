<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# GitHub Copilot Instructions for E-commerce Review System

## Project Overview
This is a Spring Boot e-commerce application with product review and rating functionality. The project is designed for learning advanced GitHub Copilot features and Test-Driven Development (TDD).

## Key Features
- User authentication and authorization
- Product management with categories
- Order processing system
- Product review and rating system (to be implemented)
- Dashboard with statistics (to be implemented)

## Architecture Guidelines
- Follow Spring Boot best practices
- Use JPA for data persistence
- Implement proper validation using Bean Validation
- Follow RESTful API design principles
- Use DTOs for data transfer
- Implement proper exception handling

## Code Style Guidelines
- Use descriptive variable and method names
- Follow Java naming conventions
- Add proper JavaDoc for public methods
- Use proper logging levels
- Implement proper unit tests for all business logic

## Security Guidelines
- Use method-level security with @PreAuthorize
- Validate user permissions before operations
- Sanitize user inputs
- Use proper password encoding

## Testing Guidelines
- Follow TDD approach for new features
- Write unit tests for service layer
- Use integration tests for repositories
- Mock external dependencies in tests
- Aim for high test coverage

## Database Guidelines
- Use appropriate JPA relationships
- Implement proper cascade operations
- Use native queries only when necessary
- Consider performance implications of queries

## Lab-Specific Instructions
This project is part of a GitHub Copilot lab focusing on:
1. Planning implementation with @workspace
2. TDD implementation of review system
3. Logic integration for average rating calculation
4. Advanced querying for dashboard API
5. Code review and refinement

When implementing features, consider:
- Business rules (e.g., users must purchase products before reviewing)
- Data integrity constraints
- Performance optimization
- Proper error handling and user feedback
