**📘Ledger Core System (Fintech Double-Entry Ledger)
🚀 Overview**

The Ledger Core System is a backend financial engine built using Java 21, Spring Boot, and PostgreSQL that simulates real-world fintech accounting systems.
It implements double-entry bookkeeping, ensuring every financial transaction is balanced, immutable, and traceable.
The system supports:

Payments (card / wallet)
Refunds (reversal of transactions)
Transfers (user-to-user movement)
Merchant settlements
Fees handling
Idempotent API design
Concurrency-safe operations

**🧠 Core Design Principles
✔ Double-Entry Accounting**

Every transaction follows:
Debit = Credit
Example:
User pays 100
Merchant receives 97
Fee account receives 3

**Immutable Ledger**
No UPDATE or DELETE allowed on ledger entries
Only INSERT operations (append-only system)
Ensures auditability and financial integrity
Total Debit = 100
Total Credit = 100.

**✔Idempotency**
Every request includes an idempotencyKey:
Prevents duplicate transactions
Ensures safe retries
Guarantees exactly-once processing behavior.

**✔ Concurrency Safety**
Uses Pessimistic Locking
Prevents race conditions on account balances
Ensures consistency in concurrent transactions.

**🏗️ Architecture**
Controller Layer
        ↓
Service Layer
        ↓
Ledger Engine (Business Logic)
        ↓
Repository Layer (JPA)
        ↓
PostgreSQL Database.

**⚙️ Tech Stack**
**Layer	        Technology **
Language	    Java 21
Framework	    Spring Boot 3.5.x
Database	    PostgreSQL
ORM	Spring    Data JPA
Migration	    Flyway
Validation	  Jakarta Validation
Build Tool	  Maven
API Style	    REST (JSON).

**📡 API Endpoints
💳 Payment**
POST /transactions/payment
{
  "userAccountId": 1,
  "merchantAccountId": 2,
  "amount": 100.00,
  "fee": 3.00,
  "idempotencyKey": "PAY-1001"
}

**🔄 Refund**
POST /transactions/refund
{
  "originalTransactionId": 1,
  "amount": 100.00,
  "idempotencyKey": "REF-1001"
}

**🔁 Transfer**
POST /transactions/transfer
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 50.00,
  "idempotencyKey": "TRF-1001"
}

**🏦 Settlement**
POST /transactions/settlement
{
  "merchantAccountId": 2,
  "amount": 200.00,
  "idempotencyKey": "SET-1001"
}
**📊 Ledger Inquiry**
GET /accounts/{id}/ledger

**🧾 Database Schema (Core Tables)**
**Accounts**
id
account_number
account_type (USER, MERCHANT, FEES, SETTLEMENT)
balance (optional computed)

**Ledger Transaction**
id
transaction_reference (UUID)
transaction_type
timestamp

**Ledger Entry**
id
transaction_id
account_id
entry_type (DEBIT / CREDIT)
amount

**Idempotency Keys**
id
key_value (unique)
transaction_reference
