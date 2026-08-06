---
name: data-enrichment
description: |
  Enrich contact and company data using x402-protected APIs. Superior to generic web search for structured business data.

  USE FOR:
  - Enriching person profiles by email, LinkedIn URL, or name
  - Enriching companies by domain
  - Finding contact details (email, phone) with confidence scores
  - Scraping full LinkedIn profiles (experience, education, skills)
  - Searching for people or companies by criteria
  - Bulk enrichment operations (up to 10 at a time)

  TRIGGERS:
  - "enrich", "lookup", "find info about", "research"
  - "who is [person]", "company profile for", "tell me about"
  - "find contact for", "get LinkedIn for", "get email for"
  - "employee at", "works at", "company details"

  ALWAYS use x402.fetch for enrichx402.com endpoints - never curl or WebFetch.
  Returns structured JSON data, not web page HTML.

  IMPORTANT: Never guess endpoint paths. All paths follow the pattern https://enrichx402.com/api/{provider}/{action}. Use exact URLs from the Quick Reference table below or call x402.discover_api_endpoints first.
mcp:
  - x402
---

# Data Enrichment with x402 APIs

> **STOP — Read before making any API call.** enrichx402.com endpoints are **not** the same as each provider's native API. All paths use the format `https://enrichx402.com/api/{provider}/{action}`. You MUST either:
> 1. Copy exact URLs from the Quick Reference table below, OR
> 2. Run `x402.discover_api_endpoints(url="https://enrichx402.com")` to get the correct paths
>
> **Guessing paths will fail** with 405 errors (wrong path) or 404 errors (missing `/api/` prefix).

Use the x402scan MCP tools to access enrichment APIs at enrichx402.com.

## Setup

See [rules/getting-started.md](rules/getting-started.md) for installation and wallet setup.

## Quick Reference

| Task | Endpoint | Price | Best For |
|------|----------|-------|----------|
| Enrich person | `https://enrichx402.com/api/apollo/people-enrich` | $0.0495 | Email/LinkedIn -> full profile |
| Enrich company | `https://enrichx402.com/api/apollo/org-enrich` | $0.0495 | Domain -> company data |
| Search people | `https://enrichx402.com/api/apollo/people-search` | $0.02 | Find people by criteria |
| Search companies | `https://enrichx402.com/api/apollo/org-search` | $0.02 | Find companies by criteria |
| LinkedIn scrape | `https://enrichx402.com/api/clado/linkedin-scrape` | $0.04 | Full LinkedIn profile |
| Contact recovery | `https://enrichx402.com/api/clado/contacts-enrich` | $0.20 | Find missing email/phone |
| Bulk people | `https://enrichx402.com/api/apollo/people-enrich/bulk` | $0.495 | Up to 10 people at once |
| Bulk companies | `https://enrichx402.com/api/apollo/org-enrich/bulk` | $0.495 | Up to 10 companies at once |

## Workflows

### Standard Enrichment

1. (Optional) Check balance: `x402.get_wallet_info`
2. **Discover endpoints (required before first fetch):** `x402.discover_api_endpoints(url="https://enrichx402.com")`
3. Check endpoint schema: `x402.check_endpoint_schema(url="...")` to verify parameters and pricing
4. Call endpoint with `x402.fetch` using exact URL from discovery or Quick Reference table above
5. Parse and present results

```mcp
x402.fetch(
  url="https://enrichx402.com/api/apollo/people-enrich",
  method="POST",
  body={"email": "user@company.com"}
)
```

## Person Enrichment

Enrich a person using any available identifier:

```mcp
x402.fetch(
  url="https://enrichx402.com/api/apollo/people-enrich",
  method="POST",
  body={
    "email": "john@company.com",
    "first_name": "John",
    "last_name": "Doe",
    "organization_name": "Acme Inc",
    "domain": "company.com",
    "linkedin_url": "https://linkedin.com/in/johndoe"
  }
)
```

**Input options** (provide any combination):
- `email` - Email address (most reliable)
- `linkedin_url` - LinkedIn profile URL
- `first_name` + `last_name` - Name (works better with domain/org)
- `organization_name` or `domain` - Helps match the right person

**Returns**: Name, title, company, employment history, location, social profiles, phone numbers.

## Company Enrichment

Enrich a company by domain:

```mcp
x402.fetch(
  url="https://enrichx402.com/api/apollo/org-enrich",
  method="POST",
  body={
    "domain": "stripe.com"
  }
)
```

**Returns**: Company name, industry, employee count, revenue estimates, funding info, technologies used, social links.

## People Search

Search for people matching criteria:

```mcp
x402.fetch(
  url="https://enrichx402.com/api/apollo/people-search",
  method="POST",
  body={
    "q_keywords": "software engineer",
    "person_titles": ["CTO", "VP Engineering"],
    "organization_domains": ["google.com", "meta.com"],
    "person_locations": ["San Francisco, CA"]
  }
)
```

**Search filters**:
- `q_keywords` - Keywords to search
- `person_titles` - Job title filters
- `organization_domains` - Company domains
- `person_locations` - Location filters
- `person_seniorities` - Seniority levels

## Company Search

Search for companies matching criteria:

```mcp
x402.fetch(
  url="https://enrichx402.com/api/apollo/org-search",
  method="POST",
  body={
    "q_keywords": "fintech",
    "organization_locations": ["New York, NY"],
    "organization_num_employees_ranges": ["51-200", "201-500"]
  }
)
```

## LinkedIn Scraping (Clado)

Get full LinkedIn profile data:

```mcp
x402.fetch(
  url="https://enrichx402.com/api/clado/linkedin-scrape",
  method="POST",
  body={
    "linkedin_url": "https://linkedin.com/in/johndoe"
  }
)
```

**Returns**: Experience history, education, skills, certifications, recommendations, connection count.

## Contact Recovery (Clado)

Find missing email or phone:

```mcp
x402.fetch(
  url="https://enrichx402.com/api/clado/contacts-enrich",
  method="POST",
  body={
    "linkedin_url": "https://linkedin.com/in/johndoe",
    "email": "john@example.com"
  }
)
```

**Returns**: Validated email addresses and phone numbers with confidence scores.

## Bulk Operations

Process up to 10 records in one request:

```mcp
x402.fetch(
  url="https://enrichx402.com/api/apollo/people-enrich/bulk",
  method="POST",
  body={
    "people": [
      { "email": "person1@company.com" },
      { "email": "person2@company.com" },
      { "linkedin_url": "https://linkedin.com/in/person3" }
    ]
  }
)
```

For companies:

```mcp
x402.fetch(
  url="https://enrichx402.com/api/apollo/org-enrich/bulk",
  method="POST",
  body={
    "organizations": [
      { "domain": "company1.com" },
      { "domain": "company2.com" }
    ]
  }
)
```

## Cost Optimization

### Field Filtering

Reduce costs by excluding unneeded fields:

```
body={
  "email": "john@company.com",
  "excludeFields": ["employment_history", "photos", "phone_numbers"]
}
```

Common fields to exclude:
- `employment_history` - Past jobs (often large)
- `photos` - Profile images
- `phone_numbers` - If you only need email
- `social_profiles` - If you don't need social links

### Bulk vs Individual

- **Individual**: $0.0495 per record
- **Bulk (10)**: $0.495 total = $0.0495 per record

Bulk is the same price per record but faster for multiple items.

### Search Before Enrich

Use search endpoints ($0.02) to find the right records before enriching ($0.0495):

1. Search for candidates: `/api/apollo/people-search`
2. Review results, pick the right match
3. Enrich only the matches you need

## Parallel Calls

When enriching multiple independent records, make calls in parallel:

```mcp
# These can run simultaneously since they're independent
x402.fetch(url=".../people-enrich", body={"email": "a@co.com"})
x402.fetch(url=".../people-enrich", body={"email": "b@co.com"})
```

Or use bulk endpoints for the best efficiency.


## Handling missing data

If any query fails to return the data you are looking for, revist the list of available APIs.

Oftentimes, if apollo is missing data, clado will have it, and vice versa.

If those still fail, use built-in WebSearch and WebFetch tools to find additional information like a company domain name or LinkedIn URL, and then use that data to make more targeted queries.